(ns com.climate.geojson-schema.test.alter-data)

(defn add-bbox-to-geojson
  [geojson]
  (assoc geojson :bbox [-180.0 200.0 100.10 1012.0]))
