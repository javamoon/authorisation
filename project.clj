(defproject ringtest "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
		 [ring "1.3.1"]
                 [org.clojure/data.json "0.2.5"]
		 [org.clojure/java.jdbc "0.3.5"]  
                 [mysql/mysql-connector-java "5.1.30"]]
  :main ringtest.core)
