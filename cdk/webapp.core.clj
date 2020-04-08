(ns webapp.core
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [stedi.cdk.alpha :as cdk]))

(cdk/import [[App Construct Duration Stack] :from "@aws-cdk/core"]
            [[Bucket] :from "@aws-cdk/aws-s3"]
            [[Code Function Runtime Tracing] :from "@aws-cdk/aws-lambda"]
            [[LambdaRestApi] :from "@aws-cdk/aws-apigateway"])

(def code
  (let [code-path "target/"]
    (Code/fromAsset code-path)))

(def app (App))

(def stack (Stack app "web-lambda"))

(def bucket (Bucket stack "web-lambda-bucket"))

(def web-fn
  (Function stack
            "web-fn"
            {:code        code
             :handler     "target/main.handler"
             :runtime     (:NODEJS_10_X Runtime)
             :environment {"BUCKET" (:bucketName bucket)}
             :memorySize 128
             :timeout (Duration/seconds 10)
             }))

(Bucket/grantWrite bucket web-fn)

(def api (LambdaRestApi stack "web-lambda-api" {:handler web-fn}))

