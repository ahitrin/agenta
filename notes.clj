(require '[nextjournal.clerk :as clerk])

;; start Clerk webserver
(clerk/serve! {:browse true
               :watch-paths ["notebooks"]})

(clerk/show! "notebooks/01.clj")
