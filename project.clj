(defproject geojson-schema "0.1.1"
  :description "A Geojson validator using prismatic schema"
  :url "https://github.com/TheClimateCorporation/geojson-schema"
  :license {:name "Apache 2.0 License"
            :url "http://opensource.org/licenses/Apache-2.0"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [prismatic/schema "0.2.6"]]
  :profiles {:test {:dependencies [[cheshire "5.3.1"]]
                    :resource-paths ["resources" "test-resources"]}})
