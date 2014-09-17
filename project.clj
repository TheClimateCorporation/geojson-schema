;  Copyright 2014 The Climate Corporation
;
;  Licensed under the Apache License, Version 2.0 (the "License");
;  you may not use this file except in compliance with the License.
;  You may obtain a copy of the License at
;
;      http://www.apache.org/licenses/LICENSE-2.0
;
;  Unless required by applicable law or agreed to in writing, software
;  distributed under the License is distributed on an "AS IS" BASIS,
;  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
;  See the License for the specific language governing permissions and
;  limitations under the License.

(defproject geojson-schema "1.0.0-SNAPSHOT"
  :description "A Geojson validator using prismatic schema"
  :url "https://github.com/TheClimateCorporation/geojson-schema"
  :license {:name "Apache 2.0"
            :url "http://www.opensource.org/licenses/mit-license.php"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [prismatic/schema "0.2.6"]]
  :profiles {:dev {:dependencies [[cheshire "5.3.1"]]}})
                   
