(ns testing.runner
  (:require [cljs.test :refer-macros [deftest testing is run-tests]]
            [com.climate.geojson-schema.test.geojson-cljs]))

(def result (atom -1))

(defmethod cljs.test/report [:cljs.test/default :end-run-tests] [m]
           (if (cljs.test/successful? m)
             (do (println "Success!")
                 (reset! result 0))
             (do (println "FAIL")
                 (reset! result 1))))

(enable-console-print!)
(defn ^:export execute []
      (.log js/console "Running Clojurescript tests")
      (run-tests 'com.climate.geojson-schema.test.geojson-cljs)
      @result)