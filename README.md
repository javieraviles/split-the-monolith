# split-the-monolith
Everyday is getting more and more popular and, **sometimes**, worthy and useful -> **split a monolith into microservices**. In this repo I will show an example of how to split a monolith into microservices using the **Strangler Fig** and **Branch By Abstraction** patterns. It will take three stages. And of course, will introduce some cloud stack along the way, as this is as well something super common nowadays.

There is a whole variety of technologies out there, for this example I will use:
 - SpringBoot (microservices themselves).
 - After split -> Docker, K8s, and NGINX as ingress proxy to redirect traffic to each ms.

Everything will be in the master branch, having a specific `tag` for each Phase once finished.

- [split-the-monolith](#split-the-monolith)
  - [Phase 1 - THE MONOLITH](#phase-1---the-monolith)
  - [Phase 2 - Applying STRANGLER FIG](#phase-2---applying-strangler-fig)
    - [The new Microservice, OrdersMs](#the-new-microservice-ordersms)
    - [The proxy, NGINX as K8s Ingress](#the-proxy-nginx-as-k8s-ingress)
  - [Phase 3 - Applying BRANCH BY ABSTRACTION](#phase-3---applying-branch-by-abstraction)

## Phase 1 - THE MONOLITH
This is a very simple SpringBoot project to dispatch `Orders`. Using H2 as database for simplicity, the main class will populate some data into the database on startup for testing purposes. A customer, a product and an order.

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

Customer and Product have to be in place before creating an order. If not enough stock in the product or credit in the customer, an exception will be thrown. The core logic of the system is in the `OrderSaga.java` class which will attempt to create an Order in one transaction.

Notice a PATCH method endpoint for both `Customer` and `Product` in their controllers to update credit/stock.

Additionally, an email notification will be sent to a customer whenever the system adds some credit to the customer. Such notificatons are carried out via `NotificationService.java`. The implementation is not there, as it is not relevant here, so I will just log a message instead. BUT, it is still important the fact that the service exists as the **Branch By Abstraction** pattern will be applied over the notification feature.

Simple nice and clean, why splitting right? well is just an example.

## Phase 2 - Applying STRANGLER FIG
The goal here is to get some responsibilities out of the original monolith, so I want to extract the `orders` management into a separate microservice.

Based on [Martin Fowler Strangler Fig post](https://martinfowler.com/bliki/StranglerFigApplication.html), we will "gradually create a new system around the edges of the old".

![Strangler Fig Pattern](https://raw.githubusercontent.com/javieraviles/split-the-monolith/master/images/strangler-fig.jpg)

A proxy will still forward `/customers` and `/products` api requests to the monolith, but `/orders` should then go to the new microservice.

Obviously, the new service will have to do some internal requests to the monolith, updating credit and stock of customers and products, and get some information; should handle orders in an independant DB, though. **The idea is we do not need to touch or modify the monolith**.

### The new Microservice, OrdersMs
A separate SpringBoot project, containing DTOs for customer and product, and one entity `Order.java`.

Only one REST controller for Order will be created here, and again the core logic of the system is in the `OrderSaga.java` class which will attempt to create an Order. We can´t use a transaction as we do in the monolith, so will use a rest client which will attempt to get credit and stock from the monolith. If something goes wrong we will need to perform a compensation now.

The idea is, even though the implementation is different, we will get the same exception for the same use cases, so the external api remains exactly the same. "Customer and Product have to be in place before creating an order. If not enough stock in the product or credit in the customer, an exception will be thrown".  Again have a look at `IntegrationTest.java` to see some use-cases.

The monolith url will be set thorugh an environment variable so it's more flexible for us to set it later to a different value.

This way we have a separate microservice ready to take all `/products` requests.

### The proxy, NGINX as K8s Ingress
If you are not familiar with kubernetes, the Ingress-NGINX implementation might not seem straightforward to you, but the underlaying NGINX functionality is still the same. It will just forward traffic to one microservice or another depending on the path. Let's have a look at the `k8s/ingress.yml`:

```
apiVersion: extensions/v1beta1  
kind: Ingress  
metadata:  
  name: split-the-monolith-ingress
  annotations:
    kubernetes.io/ingress.class: "nginx"
spec:  
  rules:
  - host: split-the-monolith.com
    http:
      paths:
      - path: /customers
        backend:
          serviceName: monolith
          servicePort: 8080
      - path: /products
        backend:
          serviceName: monolith
          servicePort: 8080
      - path: /orders
        backend:
          serviceName: ordersms
          servicePort: 8090
```

`Monolith` and `ordersms` have already been declared as services running in k8s respectively in `k8s/monolith.yaml` amd `k8s/ordersms.yaml`. I'm using minikube with Ingress addon as local dev environment, and setting `split-the-monolith.com` to my VM ip in my hosts file.

Please note that both services had also been Dockerized including a `Dockerfile` into each project and it's been pushed to dockerhub so k8s yaml images can find them somewhere.

Now is all set, functionality remains the same as in the monolith, and we have two microservices, each one taking care of a task, all behind a proxy, nice!!

In order to have some **"end to end"** tests, not only integration tests on each microservice, I've added a Postman collection in the `integrationtests` directory, which can be run against a classic local environment, starting up both microservices from your IDE in localhost (here tell postman collection to use the `env/LocalDev.json` environment file) or against a local k8s using ingress (here tell postman collection to use the `env/K8sDev.json` environment file).

Use Newman to easily execute them:

```
npm install -g Newman

newman run integrationtests/split-the-monolith.postman_collection.json -k -e integrationtests/env/K8sDev.postman_environment.json

```

## Phase 3 - Applying BRANCH BY ABSTRACTION
Remember the `NotificationService` in the monolith? well, that could very well be another microservice, just in charge of sending notifications, so the monolith does not need to have such responsibility anymore. Would be nice to spin up the new microservice and gradually switch notifications generation from monolith to this new service, using a [feature toggle](https://martinfowler.com/articles/feature-toggles.html).

Based on [Martin Fowler Branch by Abstraction post](https://martinfowler.com/bliki/BranchByAbstraction.html), "While we are building the new feature we can use FeatureToggles to run the new supplier in test environments and compare its behavior to the flawed supplier".

![Branch by Abstraction Pattern](https://raw.githubusercontent.com/javieraviles/split-the-monolith/master/images/branch-by-abstraction.png)

And so we will do in this phase 3. After creating another SpringBoot project, the simplest one (`NotificationController` to receive a POST for notifications creation, calling a service that actually sends the notification), we will then introduce a `feature toggle` in the monolith. This way, when some credit is added to a user, depending on the value of the feature toggle, the notification will get sent through the monolith implementation (still there) or through the new service (the monolith will trigger a POST to /notificationsmsUrl) so the new microservice takes care of this.

For us the feature toggle will just be an application.property called `use.notification.service`, containing a boolean:

```
@Value(value = "${use.notification.service}")
private boolean useNotificationService;

...


if (useNotificationService) {
  // rest call to new microservice
  notificationsMsClient.sendNotification(notification);
} else {
  // monolith sends the notificaton itself
  notificationService.sendEmailNotification(notification);
}
```

This way, the k8s deployment yaml for the monolith will now contain an environment variable that will override the feature toggle value per environment:

```
- env:
  - name: NOTIFICATIONSMS_URL
    value: http://notificationsms:8070
  - name: USE_NOTIFICATION_SERVICE
    value: "true"
  image: javieraviles/monolith
  name: monolith
  imagePullPolicy: Always
  ports:
    - containerPort: 8080
  securityContext:
    allowPrivilegeEscalation: false
```

So operations will have now the option to configure whether to use this external notifications service or not from there. The idea is to reach a point where no more clients are using the original monolith notifications feature so it can be removed.

An additional `k8s/notificationsms.yaml` file has been added to deploy the new microservice. Please note the `type: ClusterIP` in there, as this notifications microservice does not need to get any external traffic, will only receieve internal requests.