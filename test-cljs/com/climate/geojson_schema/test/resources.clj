(ns com.climate.geojson-schema.test.resources
  (:require [clojure.java.io :refer [resource file]]
            [cheshire.core :as json]))

(defmacro all-resources
  "Eagerly pulls in all of the geojson_examples.
  It will contain a map short-file -> file-data"
  []
  (let [data (->> (resource "geojson_examples")
                  file
                  file-seq
                  (remove #(.isDirectory %))
                  (map (juxt #(.getName %) #(json/parse-string (slurp %) true)))
                  (into {}))]
    `~data))
