(ns ecommerce.class7
  (:use clojure.pprint)
  (:require [datomic.api :as db]
            [ecommerce.model :as model]
            [ecommerce.database :as database]))

(database/delete-database)

(def connection (database/open-connection))
(database/create-schema connection)                         ; Evolving schema with product/keyword

(let [dom-casmurro (model/new-product "Livro Dom Casmurro", "/casmurro", 35M)
      o-aleph (model/new-product "O Aleph", "/aleph", 20M)]
  (db/transact connection [dom-casmurro o-aleph]))

; Books above without keywords.
(pprint (database/books (db/db connection)))

(db/transact connection [[:db/add 17592186045418 :product/keyword "Literatura Nacional"]
                         [:db/add 17592186045418 :product/keyword "Machado de Assis"]
                         [:db/add 17592186045418 :product/keyword "Prêmio Nobel"]]) ; Wrong.

; Should retrieve Dom Casmurro with 3 keywords.
(pprint (database/books (db/db connection)))

(db/transact connection [[:db/retract 17592186045418 :product/keyword "Prêmio Nobel"]])

; Should retrieve Dom Casmurro with 2 correct keywords now.
(pprint (database/books (db/db connection)))

(db/transact connection [[:db/add 17592186045419 :product/keyword "livro-fisico"]
                         [:db/add 17592186045418 :product/keyword "livro-fisico"]])

; Should return 2 books.
(println "Books with keyword livro-fisico:")
(pprint (database/books-by-keyword (db/db connection)))

(db/transact connection [{:product/name "A Metamorfose", :product/price 40M, :product/keyword "Kafka"}])

(pprint "Should return 3 books (All have keywords!)")
(pprint (database/books (db/db connection)))

(pprint "Should return A Metamorfose because the keyword: Kafka")
(pprint (database/books-by-specific-keyword (db/db connection) "Kafka"))
