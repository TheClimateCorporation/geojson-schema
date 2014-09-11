(ns geojson-schema.core
  (:require 
    [schema.core :refer [Any optional-key required-key eq one pred both either maybe]]))

;;TBD, there's a requirement that a CRS not be overridden on a sub object per the spec.
;; Not sure how to implement that yet.
(def ^:private geojson-crs Any)

(def ^:private position [Number])


(def ^:private geojson-base
  {(optional-key :crs) geojson-crs
   (optional-key :bbox) [Number]
   })

(def Point 
  (merge {(required-key :coordinates) position
          (required-key :type) (eq "Point")} 
         geojson-base))

(def MultiPoint
  (merge {(required-key :coordinates) [position]
          (required-key :type) (eq "MultiPoint")} 
         geojson-base)) 


(def ^:private linear-string-coordinates 
  [(one position "first") 
   (one position "second")
   position])

(def LineString
  (merge {(required-key :coordinates) linear-string-coordinates
          (required-key :type) (eq "LineString")}
         geojson-base))


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
(defn ^:private closed-loop
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
  (merge {(required-key :coordinates) linear-ring-coordinates
          (required-key :type) (eq "LineString")}
         geojson-base))


(def MultiLineString
  (merge {(required-key :coordinates) [linear-string-coordinates]
          (required-key :type) (eq "MultiLineString")}
         geojson-base))

(def ^:private polygon-coords 
  [linear-ring-coordinates])

(def Polygon
  (merge {(required-key :coordinates) polygon-coords
          (required-key :type) (eq "Polygon")}
         geojson-base))

(def MultiPolygon
  (merge {(required-key :coordinates) [polygon-coords]
          (required-key :type) (eq "MultiPolygon")}
         geojson-base))

(def Geometry 
  (either Point 
          MultiPoint 
          LineString 
          MultiLineString
          Polygon
          MultiPolygon))

(def GeometryCollection
  (merge {(required-key :geometries) [Geometry]
          (required-key :type) (eq "GeometryCollection")}
         geojson-base))

(def Feature
  (merge {(required-key :geometry) Geometry
          (required-key :type) (eq "Feature")
          (required-key :properties) (maybe Any)
          (optional-key :id) (maybe Any)}
         geojson-base))

(def FeatureCollection 
  (merge {(required-key :features) [Feature]
          (required-key :type) (eq "FeatureCollection")
          }
         geojson-base))

(def Geojson (either Geometry
                     GeometryCollection
                     Feature
                     FeatureCollection))
