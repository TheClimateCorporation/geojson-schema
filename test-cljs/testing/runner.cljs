(ns testing.runner
  (:require [cljs.test :as test]
            [doo.runner :refer-macros [doo-all-tests doo-tests]]
            [com.climate.geojson-schema.test.geojson-cljs]))

(doo-tests 'com.climate.geojson-schema.test.geojson-cljs)
