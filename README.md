# geojson-schema

A geojson schema for validating data using prismatic schema.

## Usage


```clojure
(require '[geojson-schema.core :refer [MultiPolygon Geojson]]); nil
(require '[schema.core :as s]); nil
(def multipolygon-example
  {:type "MultiPolygon", 
   :coordinates [[[[102.0 2.0] [103.0 2.0] [103.0 3.0] [102.0 3.0] [102.0 2.0]]] 
                 [[[100.0 0.0] [101.0 0.0] [101.0 1.0] [100.0 1.0] [100.0 0.0]] 
                  [[100.2 0.2] [100.8 0.2] [100.8 0.8] [100.2 0.8] [100.2 0.2]]]]})
;; => user/multipolygon-example

(s/validate MultiPolygon multipolygon-example)
;; => {:coordinates [[[[102.0 2.0] [103.0 2.0] [103.0 3.0] [102.0 3.0] [102.0 2.0]]] 
;;                   [[[100.0 0.0] [101.0 0.0] [101.0 1.0] [100.0 1.0] [100.0 0.0]] 
;;                    [[100.2 0.2] [100.8 0.2] [100.8 0.8] [100.2 0.8] [100.2 0.2]]]], 
;;     :type "MultiPolygon"}

(def not-really-a-multipolygon
 {:type "Point"
  :coordinates [100.0 0.0]})

(s/validate MultiPolygon not-really-a-multipolygon)
; => ExceptionInfo Value does not match schema: {:type (not (= "MultiPolygon" "Point")), :coordinates [(not (sequential? 100.0)) (not (sequential? 0.0))]}  schema.core/validate (core.clj:165)


;;But it's still valid Geojson
(s/validate Geojson not-really-a-multipolygon)
; => {:coordinates [100.0 0.0], :type "Point"}

```

## Caveats

A Coordinate Reference System (CRS) can be specified, but the format is not verified by this schema.
Similarly, the CRS is supposed to only be defined on the base object and not any constituent 
elements, but this is not enforced by the current schema.

## License

Copyright (C) 2014 The Climate Corporation. Distributed under the Apache License, Version 2.0. You may not use this library except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

See the NOTICE file distributed with this work for additional information regarding copyright ownership. Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
