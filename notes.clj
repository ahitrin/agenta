(require '[nextjournal.clerk :as clerk])

;; start Clerk webserver
(clerk/serve! {:browse true})

(clerk/show! "notebooks/01.clj")
