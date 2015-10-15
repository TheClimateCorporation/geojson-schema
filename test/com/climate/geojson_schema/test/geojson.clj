;  Copyright 2014 The Climate Corporation

;  Licensed under the Apache License, Version 2.0 (the "License");
;  you may not use this file except in compliance with the License.
;  You may obtain a copy of the License at

;      http://www.apache.org/licenses/LICENSE-2.0

;  Unless required by applicable law or agreed to in writing, software
;  distributed under the License is distributed on an "AS IS" BASIS,
;  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
;  See the License for the specific language governing permissions and
;  limitations under the License.

(ns com.climate.geojson-schema.test.geojson
  (:require [clojure.java.io :refer [resource file]]
            [cheshire.core :as json]
            [com.climate.geojson-schema.core :as geojson-schema]
            [com.climate.geojson-schema.test.alter-data :refer :all]
            [schema.core :refer [validate]]
            [clojure.test :refer :all]))

(def all-geojson-examples
  "Returns a list of all paths of every geojson example"
  (->> (resource "geojson_examples")
       file
       file-seq
       (remove #(.isDirectory %))))

(defn- check-all-spec-examples
  [test]
  (doseq [file-path all-geojson-examples]
    (let [body (slurp file-path)
          geojson (json/parse-string body true)]
        (is (test geojson) (str file-path)))))

(deftest examples-are-valid-geojson
  (check-all-spec-examples #(validate geojson-schema/GeoJSON %)))

(deftest supports-crs
  (testing "All examples are valid after adding crs"
    (check-all-spec-examples #(validate geojson-schema/GeoJSON (add-crs-to-geojson %)))))

(deftest supports-boundingbox
  (testing "All examples are valid after adding bbox"
    (check-all-spec-examples #(validate geojson-schema/GeoJSON
                                        (add-bbox-to-geojson %)))))

(defn- load-example
  "Loads resource located at path (in geojson_examples) and converts to map"
  [path]
  (-> (str "geojson_examples/" path)
      resource
      file
      slurp
      (json/parse-string  true)))

(deftest line-strings
  (deftest line-strings-schema
    (is (validate geojson-schema/LineString (load-example "linestring.geojson"))))

  (deftest linestring-need-two-coords
    (is (thrown-with-msg? Exception
                          #"Value does not match schema"
                          (validate geojson-schema/LineString
                                    {:type "LineString"
                                     :coordinates [[101.0 1.0]]})))))

(deftest linear-rings-schema
  (let [linear-ring {:type "LineString"
                     :coordinates [[100.0 0.0]
                                   [101.0 1.0]
                                   [101.4 203.0]
                                   [100.0 0.0]]}]
    (is (validate geojson-schema/LinearRing linear-ring))
    (is (validate geojson-schema/LineString linear-ring)
        "Linear Rings should also be Line Strings")))

(deftest linear-rings-are-closed
  (let [not-closed-linear-ring {:type "LineString"
                                :coordinates [[100.0 0.0]
                                              [101.0 1.0]
                                              [101.4 203.0]
                                              [101.5 0.0]
                                              [103.0 401.0]]}]
    (is (thrown-with-msg? Exception
                          #"Value does not match schema"
                          (validate geojson-schema/LinearRing
                                    not-closed-linear-ring)))))

(deftest linear-rings-have-area
  (testing "linear-rings which don't define an area"
    (let [one-dimensional-line-segment {:type "LineString"
                                        :coordinates [[100.0 0.0]
                                                      [101.0 1.0]
                                                      [100.0 0.0]]}]
      (is (thrown-with-msg? Exception
                            #"Value does not match schema"
                            (validate geojson-schema/LinearRing
                                      one-dimensional-line-segment))))))

(deftest multiline-strings
  (is (validate geojson-schema/MultiLineString (load-example "multiline_string.geojson"))))

(deftest polygons-are-valid
  (is (validate geojson-schema/Polygon (load-example "polygon_noholes.geojson")))
  (is (validate geojson-schema/Polygon (load-example "polygon_holes.geojson"))))

(deftest multipolygon-is-valid
  (is (validate geojson-schema/MultiPolygon (load-example "multipolygon.geojson"))))

(deftest geometrycollection-is-valid
  (is (validate geojson-schema/GeometryCollection
                (load-example "geometrycollection.geojson"))))

(deftest feature-is-valid
  (is (validate geojson-schema/Feature (load-example "feature.geojson"))))

(deftest featurecollection-is-valid
  (let [feature-collection (load-example "featurecollection.geojson")]
    (is (validate geojson-schema/FeatureCollection feature-collection))
    (is (validate geojson-schema/GeoJSON feature-collection))))
