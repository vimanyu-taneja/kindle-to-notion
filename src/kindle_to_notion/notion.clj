(ns kindle-to-notion.notion
  (:require [clj-http.client :as http]
            [clojure.data.json :as json]
            [environ.core :refer [env]]))

(defn get-notion-db-entry [title]
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
    (json/read-str (:body response))))

(defn get-children-blocks [page-id]
  (let [token    (env :notion-key)
        url      (format "https://api.notion.com/v1/blocks/%s/children" page-id)
        headers  {"Authorization"  (str "Bearer " token)
                  "Notion-Version" "2022-06-28"}
        response (http/get
                  url
                  {:headers headers})]
    (json/read-str (:body response))))

(defn update-block-text [block-id text]
  (let [token    (env :notion-key)
        url      (str "https://api.notion.com/v1/blocks/" block-id)
        headers  {"Authorization"  (str "Bearer " token)
                  "Notion-Version" "2022-06-28"
                  :content-type    "application/json"}
        body     (json/write-str
                  {"type"      "paragraph"
                   "paragraph" {"rich_text"
                                [{"type" "text"
                                  "text" {"content" text
                                          "link"    nil}}]
                                "color"  "default"}})
        response (http/patch
                  url
                  {:headers headers
                   :body    body})]
    (json/read-str (:body response))))
