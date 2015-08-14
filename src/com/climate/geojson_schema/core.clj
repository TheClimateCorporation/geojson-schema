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
(ns com.climate.geojson-schema.core
  (:require
    [schema.core :refer [Any optional-key required-key eq one pred both either maybe named conditional]]))

(def ^:private named-crs
  {:type (eq "name")
   :properties {:name String
                Any Any}
   Any Any})

(def ^:private linked-crs
  {:type (eq "link")
   :properties {:href String
                (optional-key :type) String
                Any Any}
   Any Any})

;;TBD, there's a requirement that a CRS not be overridden on a sub object per the spec.
;; Not sure how to implement that yet.
(def ^:private geojson-crs
  (named (conditional #(= "name" (:type %)) named-crs
                      #(= "link" (:type %)) linked-crs)
         "CRSs should be either named CRSs or linked CRSs."))

(def ^:private position [Number])

(def ^:private geojson-base
  {(optional-key :crs) geojson-crs
   (optional-key :bbox) [Number]})

(def Point
  (merge geojson-base
         {:coordinates position
          :type (eq "Point")}))

(def MultiPoint
  (merge geojson-base
         {:coordinates [position]
          :type (eq "MultiPoint")}))

(def ^:private linear-string-coordinates
  [(one position "first")
   (one position "second")
   position])

(def LineString
  (merge geojson-base
         {:coordinates linear-string-coordinates
          :type (eq "LineString")}))

;; Linear Ring
;;;A Linear ring is a closed loop, so it must have
;;;at least 3 verticies.
;;;
;;;        Cord 0 *and* Cord 3
;;;                #
;;;               @ @
;;;              @   @
;;;             @     @
;;;            @       @
;;;           @         @
;;;          @           @
;;;         @             @
;;;        @               @
;;;       #                 #
;;;      @'''''''''''''''''''@
;;; Cord 1                    Cord 2
(defn- closed-loop
  "A loop is closed if it has at least 4 coordinates and the first coordinate
  is the last. There is no requirement in the spec that the closed shape not
  intersect itself."
  [coordinate-seq]
  (and (= (first coordinate-seq)
          (last coordinate-seq))
       (>= (count coordinate-seq)
          4)))

(def ^:private linear-ring-coordinates
  (both [position] (pred closed-loop 'closed)))

(def LinearRing
  (merge geojson-base
         {:coordinates linear-ring-coordinates
          :type (eq "LineString")}))

(def MultiLineString
  (merge geojson-base
         {:coordinates [linear-string-coordinates]
          :type (eq "MultiLineString")}))

(def ^:private polygon-coords
  [linear-ring-coordinates])

(def Polygon
  (merge geojson-base
         {:coordinates polygon-coords
          :type (eq "Polygon")}))

(def MultiPolygon
  (merge geojson-base
         {:coordinates [polygon-coords]
          :type (eq "MultiPolygon")}))

(def Geometry
  (either Point
          MultiPoint
          LineString
          MultiLineString
          Polygon
          MultiPolygon))

(def GeometryCollection
  (merge geojson-base
         {:geometries [Geometry]
          :type (eq "GeometryCollection")}))

(def Feature
  (merge geojson-base
         {:geometry Geometry
          :type (eq "Feature")
          :properties (maybe Any)
          (optional-key :id) (maybe Any)}))

(def FeatureCollection
  (merge geojson-base
         {:features [Feature]
          :type (eq "FeatureCollection")}))

(def GeoJSON (either Geometry
                     GeometryCollection
                     Feature
                     FeatureCollection))
