(ns ecommerce.database
  (:use clojure.pprint)
  (:require [datomic.api :as db]))

(def db-uri "datomic:dev://localhost:4334/ecommerce")

(def schema [{:db/ident       :product/name
              :db/valueType   :db.type/string
              :db/cardinality :db.cardinality/one
              :db/doc         "Product name"}
             {:db/ident       :product/slug
              :db/valueType   :db.type/string
              :db/cardinality :db.cardinality/one
              :db/doc         "Access product using HTTP slug"}
             {:db/ident       :product/price
              :db/valueType   :db.type/bigdec
              :db/cardinality :db.cardinality/one
              :db/doc         "Product price as monetary value"}
             {:db/ident :product/keyword
              :db/valueType :db.type/string
              :db/cardinality :db.cardinality/many
              :db/doc "Keywords to find products"}])

(defn open-connection []
  (db/create-database db-uri)
  (db/connect db-uri))

(defn create-schema [connection]
  (db/transact connection schema))

(defn delete-database []
  (db/delete-database db-uri))

(defn all-products [database]
  (db/q '[:find ?entity
          :where [?entity :product/name]] database))

(defn all-products-for-slug-fixed [database]
  (db/q '[:find ?entity
          :where [?entity :product/slug "/macbook"]] database))

(defn all-products-for-slug [database slug]
  (db/q '[:find ?entity
          :in $ ?slug
          :where [?entity :product/slug ?slug]] database slug))

(defn all-slugs [database]
  (db/q '[:find ?slug
          :where [_ :product/slug ?slug]] database))

(defn all-products-for-price [database]
  (db/q '[:find ?name, ?price
          :where
          [?entity :product/name ?name]
          [?entity :product/price ?price]] database))

; Retrieve data with identifiers - First Way
(defn all-products-for-price-with-identifiers [database]
  (db/q '[:find ?name, ?price
          :keys name, price                                 ; We can use keys to specify name
          :where
          [?entity :product/name ?name]
          [?entity :product/price ?price]] database))

; Second Way - Not so goood. We need to specify the attributes we want, if we need multiple attributes, the list can become much longer...
(defn all-products-for-price-with-identifiers-pull [database]
  (db/q '[:find (pull ?entity [:product/name :product/price])
          :where
          [?entity :product/name ?name]
          [?entity :product/price ?price]] database))

(defn all-products-for-price-with-identifiers-pull-generic [database]
  (db/q '[:find (pull ?entity [*])
          :where [?entity :product/name]] database))

(defn cars-price-above [database price]
  (db/q '[:find ?some-name, ?some-price
          :in $, ?minimum-price
          :keys product/name, product/price
          :where
          [?entity :product/name ?some-name]
          [?entity :product/price ?some-price]
          [(> ?some-price ?minimum-price)]] database price))

(defn books [database]
  (db/q '[:find (pull ?entity [*])
          :where
          [?entity :product/name ?some-name]
          [?entity :product/price ?some-price]] database))

(defn books-by-keyword [database]
  (db/q '[:find (pull ?entity [*])
          :where [?entity :product/keyword ?some-keyword]] database))

(defn books-by-specific-keyword [database keyword]
  (db/q '[:find (pull ?entity [*])
          :in $, ?requested-keyword
          :where
          [?entity :product/keyword ?requested-keyword]] database keyword))