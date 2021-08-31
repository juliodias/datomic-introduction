(ns ecommerce.class5
  (:use clojure.pprint)
  (:require [datomic.api :as db]
            [ecommerce.model :as model]
            [ecommerce.database :as database]))

(database/delete-database)

(def connection (database/open-connection))
(database/create-schema connection)

(let [macbook (model/new-product "MacBook PRO 15 Inch - 2021", "/macbook", 25600M)
      iphone (model/new-product "Iphone 12 Pro Max - 2021", "/iphone", 13450M)]
  (pprint @(db/transact connection [macbook iphone])))

; Snapshot with 2 elements saved. (db-after value)
(def database-in-the-past (db/db connection))

(let [airpods (model/new-product "Apple AirPods - 2021" , "/airpods", 5000M)]
  (db/transact connection [airpods]))

; Should return 3 elements
(pprint (count (database/all-products (db/db connection))))

; Should return 2 elements
(pprint (count (database/all-products database-in-the-past)))

; Before
(pprint (count (database/all-products (db/as-of (db/db connection) #inst "2021-08-30T14:27:00.200"))))

; Middle
(pprint (count (database/all-products (db/as-of (db/db connection) #inst "2021-08-30T14:28:00.200"))))

; After
(pprint (count (database/all-products (db/as-of (db/db connection) #inst "2021-08-30T14:29:00.200"))))

; Datomic is fucking awesome!