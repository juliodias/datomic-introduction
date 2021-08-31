(ns ecommerce.class6
  (:use clojure.pprint)
  (:require [datomic.api :as db]
            [ecommerce.model :as model]
            [ecommerce.database :as database]))

(database/delete-database)

(def connection (database/open-connection))
(database/create-schema connection)

(let [opala (model/new-product "Opala Comodoro", "/opala-preto", 50000M)
      fusca (model/new-product "Fusca 63", "/fusca", 34000M)
      ford-ka (model/new-product "Ford Ka", "/ka", 15000M)]
  (db/transact connection [fusca ford-ka opala]))

(pprint (database/cars-price-above (db/db connection) 40000))

; Should return Opala.
(pprint (count (database/cars-price-above (db/db connection) 40000)))

; Should return Fusca and Opala.
(pprint (count (database/cars-price-above (db/db connection) 20000)))