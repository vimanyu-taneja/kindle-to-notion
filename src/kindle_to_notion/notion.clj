(ns kindle-to-notion.notion
  (:require [clj-http.client :as http]
            [clojure.data.json :as json]
            [environ.core :refer [env]]
            [taoensso.timbre :as log]))

(defn get-notion-db-entry [title]
  (try
    (let [token    (env :notion-key)
          db-id    (env :notion-db-id)
          url      (format "https://api.notion.com/v1/databases/%s/query" db-id)
          headers  {"Authorization"  (str "Bearer " token)
                    "Notion-Version" "2022-06-28"
                    :content-type    "application/json"}
          body     (json/write-str
                    {"filter" {"property"  "Title"
                               "rich_text" {"equals" title}}})
          response (http/post
                    url
                    {:headers headers
                     :body    body})]
      (json/read-str (:body response)))
    (catch Throwable e
      (log/error "Unable to get Notion DB entry:" e))))

(defn construct-blocks [highlights]
  (map (fn [text]
         {"object"    "block"
          "type"      "paragraph"
          "paragraph" {"rich_text"
                       [{"type" "text"
                         "text" {"content" text}}]
                       "color"  "default"}})
       highlights))

(defn append-blocks [page-id highlights]
  (try
    (let [token     (env :notion-key)
          url       (format "https://api.notion.com/v1/blocks/%s/children" page-id)
          headers   {"Authorization"  (str "Bearer " token)
                     "Notion-Version" "2022-06-28"
                     :content-type    "application/json"}
          blocks    (partition-all 100 (construct-blocks highlights)) ; can send a max of 100 blocks per request
          responses (map #(http/patch
                           url
                           {:headers headers
                            :body    (json/write-str {:children %})})
                         blocks)]
      (doall (mapv #(json/read-str (:body %)) responses)))
    (catch Throwable e
      (log/error "Unable to append children blocks:" e))))
