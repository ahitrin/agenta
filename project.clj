(defproject agenta "0.1.0-SNAPSHOT"
  :description "A simple agent-oriented AI 'game'"
  :url "https://github.com/ahitrin/agenta"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [com.github.javafaker/javafaker "1.0.2"]
                 [org.apache.logging.log4j/log4j-api "2.13.3"]
                 [org.apache.logging.log4j/log4j-core "2.13.3"]]
  :java-source-paths ["src/main/java"
                      "src/test/java"]
  :main agenta.core
  :repl-options {:init-ns agenta.core})
