(ns ecommerce.core
  (:use clojure.pprint)
  (:require [datomic.api :as d]
            [ecommerce.model :as model]
            [ecommerce.database :as db]))

(def connection (db/open-connection))

(db/create-schema connection)

(let [macbook (model/new-product "Macbook Pro 15", "macbook", 15600.10M)]
  (d/transact connection [macbook]))

(def read-database (d/db connection))

(d/q '[:find ?transaction
       :where [?transaction :product/name]] read-database)

; Datomic allow us to not insert all datas but nil values are not allowed.
(let [cellphone {:product/name "Iphone 12"}]
  (d/transact connection [cellphone]))

(let [another-macbook (model/new-product "MacBook Pro 16 Inch", "/another-macbook", 20000M)
      transact-result @(d/transact connection [another-macbook])
      entity-id (first (vals (:tempids transact-result)))]  ; (-> transact-result :tempids vals first)
  (pprint @(d/transact connection [[:db/add entity-id :product/price 21500M]]))
  (pprint @(d/transact connection [[:db/retract entity-id :product/slug "/another-macbook"]]))) ; @ means we wait the transaction complete and retrieve the result. (Can be bottleneck)