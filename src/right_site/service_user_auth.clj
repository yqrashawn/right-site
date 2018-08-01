(ns right-site.service-user-auth
  (:require  [ring.util.response :as ring-resp]
             [io.pedestal.interceptor :as interceptor]
             [io.pedestal.interceptor.chain :as interceptor.chain]
             [io.pedestal.interceptor.error :refer [error-dispatch]]
             [buddy.auth :as auth]
             [buddy.auth.backends :as auth.backends]
             [buddy.auth.middleware :as auth.middleware]
             [taoensso.timbre :as timbre
              :refer [log  trace  debug  info  warn  error  fatal  report
                      logf tracef debugf infof warnf errorf fatalf reportf
                      spy get-env]]))

(require '[buddy.sign.jwt :as jwt])
(require '[cheshire.core :as json])

(def secret "19da7208-76bc-42bf-9aa2-9d8da2232e5d")

(defn jws-authfn
  ([arg] (debug arg) "haha")
  ([arg1 arg2] (debug arg1 arg2) "haha"))

(defn jws-unauthorized-handler
  [arg]
  (debug arg) "haha")

(def jws-auth-backend (auth.backends/jws {:secret secret
                                          :authfn jws-authfn
                                          :unauthorized-handler jws-unauthorized-handler
                                          :options {:skip-validation false}}))

(def authentication-interceptor
  "Port of buddy-auth's wrap-authentication middleware."
  (interceptor/interceptor
   {:name ::authenticate
    :enter (fn [ctx]
             (update ctx :request auth.middleware/authentication-request jws-auth-backend))}))

(defn authorization-interceptor
  "Port of buddy-auth's wrap-authorization middleware."
  [backend]
  (error-dispatch [ctx ex]
                  [{:exception-type :clojure.lang.ExceptionInfo :stage :enter}]
                  (try
                    (assoc ctx
                           :response
                           (auth.middleware/authorization-error (:request ctx) ex backend))
                    (catch Exception e
                      (assoc ctx ::interceptor.chain/error e)))
                  :else (assoc ctx ::interceptor.chain/error ex)))

(defn user-auth-handler
  [request]
  (debug "handler")
  (let [data (:form-params request)
        ;; user (find-user (:username data)   ;; (implementation ommited)
        ;;                 (:password data))
        user "1234"
        token (jwt/sign {:user (:id user)} secret)]
    {:status 200
     :body (json/encode {:token token})
     :headers {"Content-Type" "application/json"}}))

(defn user-login-page
  [request]
  (ring-resp/response "this is login page"))