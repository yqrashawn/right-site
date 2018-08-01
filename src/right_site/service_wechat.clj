(ns right-site.service-wechat
  (:require [ring.util.response :as ring-resp]))

(defn sha1-str [s]
  (->> (-> "sha1"
           java.security.MessageDigest/getInstance
           (.digest (.getBytes s)))
       (map #(.substring
              (Integer/toString
               (+ (bit-and % 0xff) 0x100) 16) 1))
       (apply str)))

(defn wechat-auth-handler
  [request]
  (let [{:keys [params]} request
        {:keys [signature timestamp nonce echostr]} params
        token "yqrashawn"
        sha1ed (sha1-str (reduce str (into [] (sort [token timestamp nonce]))))]
    (if (= sha1ed signature)
      (ring-resp/response (:echostr (:params request)))
      {:status 404})))

(defn wechat-user-message-handler
  [request]
  (println "wechat user message, not handled yet")
  (println request))