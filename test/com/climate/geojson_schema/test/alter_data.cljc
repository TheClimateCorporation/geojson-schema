(ns com.climate.geojson-schema.test.alter-data)

(defn add-crs-to-geojson
  [geojson]
  (assoc geojson :crs {:type       "name"
                       :properties {:name "urn:ogc:def:crs:OGC:1.3:CRS84"}}))

(defn add-bbox-to-geojson
  [geojson]
  (assoc geojson :bbox [-180.0 200.0 100.10 1012.0]))
