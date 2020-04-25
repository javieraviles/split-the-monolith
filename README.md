# split-the-monolith
Everyday is getting more and more popular and, **sometimes**, worthy and useful -> **split a monolith into microservices**. In this repo I will show an example of how to split a monolith into microservices using the **Strangler Fig** and **Branch By Abstraction** patterns. It will take three stages. And of course, will introduce some cloud stack along the way, as this is as well something super common.

There is a whole variety of technologies out there, for this example I will use:
 - SpringBoot (microservices themselves).
 - After split -> Docker, K8s, some Api Gateway (nginx or istio, will see) to redirect traffic to each ms.

Everything will be in the master branch, having a specific `tag` for each Phase once finished.

# Phase 1 - THE MONOLITH
This is a very simple SpringBoot project to dispatch `Orders`. Using H2 as db for simplicity, the main class will populate some data into the database on startup for testing purposes. A customer, a product and an order.

Three self-explaining entities:
 - **Customer**
   - name
   - credit
 - **Product**
   - name
   - stock
 - **Order**
   - customer (manyToOne)
   - totalAmount
   - product (manyToOne)
   - productQuantity

REST api to create any of the aforementioned ones in `JSON` format. Have a look at `IntegrationTest.java` to see some use-cases.

Customer and Product have to be in place before creating an order. If not enough stock in the product or credit in the customer, an exception will be thrown. The core logic of the system is in the `OrderSaga.java` class.

Simple nice and clean, why splitting right? well is just an example.