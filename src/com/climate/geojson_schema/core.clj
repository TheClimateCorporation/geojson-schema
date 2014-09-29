(ns com.climate.geojson-schema.core
  (:require
    [schema.core :refer [Any optional-key required-key eq one pred both either maybe]]))

;;TBD, there's a requirement that a CRS not be overridden on a sub object per the spec.
;; Not sure how to implement that yet.
(def ^:private geojson-crs
  {:type String
   :properties (maybe {Any Any})})

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
