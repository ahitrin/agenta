(defproject agenta "0.1.0-SNAPSHOT"
  :description "A simple agent-oriented AI 'game'"
  :url "https://github.com/ahitrin/agenta"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/tools.logging "1.1.0"]
                 [com.github.javafaker/javafaker "1.0.2"]
                 [ch.qos.logback/logback-classic "1.1.2"]
                 [org.slf4j/slf4j-api "1.7.30"]]
  :java-source-paths ["src/main/java"
                      "src/test/java"]
  :resource-paths ["resources"]
  :main agenta.core
  :aot [agenta.ImagePanel]
  :repl-options {:init-ns agenta.core})
