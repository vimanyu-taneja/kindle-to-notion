(ns kindle-to-notion.core
  (:gen-class)
  (:require [clojure.java.io :as io]
            [clojure.pprint :refer [pprint]]
            [clojure.string :as str]
            [taoensso.timbre :as log]))

(defn parse-clippings []
  (let [lines ; separate out individual clippings
        (->  (io/resource "My Clippings.txt")
             slurp
             (str/replace #"\ufeff|\r\n" "")
             (str/split #"=========="))
        items ; extract data from each clipping
        (->> lines
             (map #(re-find #"^([^\(]+\S)\s*\(([^\)]+)\)-\s*Your Highlight at location\s+(\d+)-(\d+)\s*\|\s*Added on ([A-Za-z]+,\s+\d+\s+[A-Za-z]+\s+\d{4}\s+\d+:\d+:\d+)\s*(.*)$" %))
             (remove nil?)
             (map rest)
             (map #(zipmap [:title :author :start-loc :end-loc :added-at :text] %))
             (map #(let [start (Integer/parseInt (:start-loc %))
                         end   (Integer/parseInt (:end-loc %))]
                     (assoc % :start-loc start :end-loc end))))
        groups ; group by book
        (->> items
             (group-by :title)
             (map (fn [[title items]]
                    {:title     title
                     :clippings (sort-by :start-loc items)})))]
    (log/info "Lines:"  lines)
    (log/info "Items:"  (pr-str items))
    (log/info "Groups:" (pr-str groups))
    groups))

(defn -main
  [& args]
  (pprint (parse-clippings)))
