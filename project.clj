;  Copyright 2017 The Climate Corporation

;  Licensed under the Apache License, Version 2.0 (the "License");
;  you may not use this file except in compliance with the License.
;  You may obtain a copy of the License at

;      http://www.apache.org/licenses/LICENSE-2.0

;  Unless required by applicable law or agreed to in writing, software
;  distributed under the License is distributed on an "AS IS" BASIS,
;  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
;  See the License for the specific language governing permissions and
;  limitations under the License.

(defproject com.climate/geojson-schema "0.2.0-SNAPSHOT"
  :description "A Geojson validator using prismatic schema"
  :url "https://github.com/TheClimateCorporation/geojson-schema"
  :license {:name "Apache 2.0 License"
            :url "http://opensource.org/licenses/Apache-2.0"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [prismatic/schema "1.1.6"]]
  :deploy-repositories  [["releases" :clojars]]
  :aliases {"cljs-test" ["with-profile" "test-cljs" "doo" "phantom" "once"]}
  :doo {:build "test"}
  :profiles {:test {:dependencies [[cheshire "5.5.0"]]
                    :resource-paths ["resources" "test-resources"]}
             :test-cljs {:plugins [[lein-cljsbuild "1.1.0"]
                                   [lein-doo "0.1.6-SNAPSHOT"]]
                         :dependencies [[org.clojure/clojurescript "1.7.48"]
                                        [cheshire "5.5.0"]
                                        [doo "0.1.6-SNAPSHOT"]]
                         :hooks [leiningen.cljsbuild]
                         :resource-paths ["test-resources"]
                         :cljsbuild {:builds {:test {:resource-paths ["test-resources"]
                                                     :source-paths ["src" "test" "test-cljs"]
                                                     :compiler {:output-to "target/cljs/test-output.js"
                                                                :optimizations :none
                                                                :main 'testing.runner
                                                                :pretty-print true}}}}}})
