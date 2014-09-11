(defproject geojson-schema "1.0.0-SNAPSHOT"
  :description "A Geojson validator using prismatic schema"
  :url "https://github.com/TheClimateCorporation/geojson-schema"
  :license {:name "MIT License"
            :url "http://www.opensource.org/licenses/mit-license.php"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [prismatic/schema "0.2.6"]]
  :profiles {:dev {:dependencies [[cheshire "5.3.1"]]}})
                   
