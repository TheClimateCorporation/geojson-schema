(ns com.climate.geojson-schema.test.geojson
  (:require [clojure.java.io :refer [resource file]]
            [cheshire.core :as json]
            [com.climate.geojson-schema.core :as schema]
            [schema.core :refer [validate]]
            [clojure.test :refer :all]))

;;;We validate all of the provided geojson spec examples are valid
(def all-geojson-examples
  (->>  (resource "geojson_examples")
        file
        file-seq
        (remove #(.isDirectory %))))

(defn check-all-spec-examples [test]
  (doseq [file-path all-geojson-examples]
    (let [body (slurp file-path)
          geojson (json/parse-string body true)]
        (is (test geojson) (str file-path)))))

(deftest examples-are-valid-geojson
  (check-all-spec-examples #(validate schema/GeoJSON %)))

;;; All examples are valid after adding crs
(defn add-crs-to-geojson
  [geojson]
  (assoc geojson :crs {:type "name" :properties {:name "urn:ogc:def:crs:OGC:1.3:CRS84"}}))

(deftest supports-crs
  (check-all-spec-examples (fn [geojson] (validate schema/GeoJSON (add-crs-to-geojson geojson)))))

;;; All examples are valid after adding bbox
(defn add-bbox-to-geojson
  [geojson]
  (assoc geojson :bbox [-180.0 200.0 100.10]))

(deftest supports-boundingbox
  (check-all-spec-examples (fn [geojson] (validate schema/GeoJSON (add-bbox-to-geojson geojson)))))


(defn load-example
  "Loads resource located at path (in geojson_examples) and converts to map"
  [path]
  (-> (str "geojson_examples/" path)
      resource
      file
      slurp
      (json/parse-string  true)))

;; LineString
(deftest line-strings-schema
  (is (validate schema/LineString (load-example "linestring.geojson"))))

(deftest linestring-need-two-coords
  (is (thrown-with-msg? Exception
                        #"Value does not match schema"
                        (validate schema/LineString
                                  {:type "LineString"
                                   :coordinates [[101.0 1.0]]}))))

;; LinearRing
(deftest linear-rings-schema
  (let [linear-ring {:type "LineString"
                     :coordinates [[100.0 0.0]
                                   [101.0 1.0]
                                   [101.4 203.0]
                                   [100.0 0.0]]}]
    (is (validate schema/LinearRing linear-ring))
    (is (validate schema/LineString linear-ring)
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
                          (validate schema/LinearRing
                                    not-closed-linear-ring)))))

;;; Line has too few coordinates, just leaves a point and
;;; returns. Has no area
(deftest linear-rings-have-area
  (let [one-dimensional-line-segment {:type "LineString"
                                      :coordinates [[100.0 0.0]
                                                    [101.0 1.0]
                                                    [100.0 0.0]]}]
  (is (thrown-with-msg? Exception
                        #"Value does not match schema"
                        (validate schema/LinearRing
                                  one-dimensional-line-segment)))))

;; MultiLineString
(deftest multiline-strings
  (is (validate schema/MultiLineString (load-example "multiline_string.geojson"))))

;; Polygon
(deftest polygons-are-valid
  (is (validate schema/Polygon (load-example "polygon_noholes.geojson")))
  (is (validate schema/Polygon (load-example "polygon_holes.geojson"))))

;; MultiPolygon
(deftest multipolygon-is-valid
  (is (validate schema/MultiPolygon (load-example "multipolygon.geojson"))))

;; GeometryCollection
(deftest geometrycollection-is-valid
  (is (validate schema/GeometryCollection
                (load-example "geometrycollection.geojson"))))

;; Feature
(deftest feature-is-valid
  (is (validate schema/Feature (load-example "feature.geojson"))))

;; FeatureCollection
(deftest featurecollection-is-valid
  (let [feature-collection (load-example "featurecollection.geojson")]
    (is (validate schema/FeatureCollection feature-collection))
    (is (validate schema/GeoJSON (load-example feature-collection)))))
