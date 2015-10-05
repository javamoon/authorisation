(ns ringtest.core (:gen-class))
(use '[ring.adapter.jetty :only (run-jetty)])
(require '[clojure.data.json :as json])
(require '[clojure.java.jdbc :as jdbc])
(require '[clojure.string :as cstr])

(defn html? [uri] (.endsWith uri ".html"))
(defn js? [uri] (.endsWith uri ".js"))
(defn png? [uri] (.endsWith uri ".png"))
(defn json? [uri] (.startsWith uri "/json"))
(defn path? [uri] (.startsWith uri "/path"))
(defn ico? [uri] (.equals uri "/favicon.ico"))

;sql

(def PREFIX "7DF5")

(def security-code
  #{"123456" "100" "200" "hello"})

(def formatObj (java.text.SimpleDateFormat. "yyyy-MM-dd HH:mm:ss"))

(defn checkcode [x]
  (let [code (cstr/replace x " " "")
		code (cstr/replace x "+" "")]
    (if (and (.startsWith code PREFIX)
             (= 20 (count code)))
      true false)))

(defn print-action-time [action]
  (println action (.format formatObj (java.util.Date.))))

(defn query [qstr]
  (let [i (+ 10 (.indexOf qstr "query-num="))
        j (.indexOf qstr "&" i)
        v (if (> j 0) (.substring qstr i j) (.substring qstr i))] 
    {:status 200
     :headers {"Content-Type" "application/json"}
     :body (json/json-str 
             (if (or (contains? security-code v) 
                     (checkcode v))
			   {:status true :response (str "You query string is [" v "]")}
               {:status false :response (str "You query string is [" v "]")}))}))

(defn get-json
  [uri query-string]
  (if (.endsWith uri "query-num") (query query-string)
  {:status 200
   :headers {"Content-Type" "application/json"}
   :body
  (json/json-str {:key1 123 :key2 ['a 'b 'c] :key3 {:a 1 :b 2 :c 3} :key4 #{'a 'b 'c}})}))  

(defn get-html
  [path]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body (java.io.File. path)})

(defn get-js
  [path]
  {:status 200
   :headers {"Content-Type" "application/javascript"}
   :body (java.io.File. path)})

(defn get-png
  [path]
  {:status 200
   :headers {"Content-Type" "image/png"}
   :body (java.io.File. path)})
  
(defn get-ico
  [path]
  {:status 200
   :headers {"Content-Type" "image/ico"}
   :body (java.io.File. path)})

(defn get-other
  [path]
    {:status 200
     :headers {"Content-Type" "application/octet-stream"}
     :body (java.io.File. path)})

(defn get-content
  [uri]
  (cond (html? uri) (get-html uri)
        (js? uri) (get-js uri)  
        (png? uri) (get-png uri)  
    	:else (get-other uri)))

(defn get-default []
  (get-html "path/query.html"))

(defn flylotus? [host]
  (.equals host "flylotus.com")) 

(defn www? [host]
  (.equals host "www.0564315.com")) 

(defn get-www [uri]
  (if (.equals "/" uri) 
    (get-default)
    (get-content  (str "path" uri))))

(defn app
  [{:keys [uri query-string server-name server-port remote-addr]}]
  (if (json? uri)
    (print-action-time (str server-name ":" server-port uri (if query-string (str "?" query-string) "") " [" remote-addr "]")))
  (cond (path? uri) (get-content (.substring uri 1))
        (json? uri) (get-json (.substring uri 1) query-string)
        (ico? uri) (get-ico (.substring uri 1))
        (flylotus? server-name) (get-www uri)
		(www? server-name) (get-www uri)
        :else
          {:body (format "You requested %s with query %s" uri query-string)}))

(defn run-server [] (run-jetty #'app {:port 80 :join? false}))

(defn -main [& args] (run-server))
