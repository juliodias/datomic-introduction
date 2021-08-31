(ns ecommerce.class3
  (:use clojure.pprint)
  (:require [datomic.api :as db]
            [ecommerce.database :as database]
            [ecommerce.model :as model]))

(def connection (database/open-connection))
(database/create-schema connection)

(let [macbook (model/new-product "Macbook Pro 15 Inch", "/macbook", 30000M)
      iphone (model/new-product "Iphone 12 Pro Max", "/iphone", 150000M)
      calculator {:product/name "Calculator HP 1200"}]
  (db/transact connection [macbook iphone calculator]))

(pprint (database/all-products (db/db connection)))
(pprint (database/all-products-for-slug (db/db connection) "/macbook"))

(pprint "\n")
(pprint (database/all-products-for-slug-fixed (db/db connection)))

(pprint (database/all-slugs (db/db connection)))
(pprint (database/all-products-for-price (db/db connection)))

(pprint (database/all-products-for-price-with-identifiers (db/db connection)))

(pprint "\n")
(pprint (database/all-products-for-price-with-identifiers-pull (db/db connection)))

(pprint "\n")
(pprint (database/all-products-for-price-with-identifiers-pull-generic (db/db connection)))
