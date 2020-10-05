(ns fr.jeremyschoffen.prose.alpha.document.sci
  (:require
    [medley.core :as medley]
    [fr.jeremyschoffen.prose.alpha.document.common.evaluator :as evaluator]
    [fr.jeremyschoffen.prose.alpha.document.sci.bindings :as sci-bindings :include-macros true]
    [fr.jeremyschoffen.prose.alpha.eval.common :as eval-common]
    [fr.jeremyschoffen.prose.alpha.eval.sci :as eval-sci]

    fr.jeremyschoffen.prose.alpha.document.lib))


(def sci-opt-doc-ns
  {:namespaces (sci-bindings/make-ns-bindings fr.jeremyschoffen.prose.alpha.document.lib)})


(defn init [opts]
  (let [opts (medley/deep-merge sci-opt-doc-ns opts)]
    (eval-sci/init opts)))


(def make-evaluator evaluator/make)


(comment
  (require '[clojure.java.io :as io])
  (require '[fr.jeremyschoffen.prose.alpha.reader.core :as reader])

  (def ctxt (init {}))

  (-> ctxt
      :env
      deref
      :namespaces
      (get 'fr.jeremyschoffen.prose.alpha.document.lib))

  (def load-doc (fn [path]
                  (-> path
                      io/resource
                      slurp
                      reader/read-from-string)))

  (def eval-doc (make-evaluator {:load-doc load-doc
                                 :eval-forms (partial eval-sci/eval-forms-in-temp-ns ctxt)}))

  (eval-doc "complex-doc/master.prose")


  (eval-common/bind-env {:prose.alpha.document/input {:some :input}}
    (eval-sci/eval-forms-in-temp-ns ctxt
      '[(require '[fr.jeremyschoffen.prose.alpha.document.lib :refer [get-input]])
        (get-input)])))