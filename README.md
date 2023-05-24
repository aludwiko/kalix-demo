# Kalix Order Saga

## Designing

To understand the Kalix concepts that are the basis for this example,
see [Designing services](https://docs.kalix.io/java/development-process.html) in the documentation.

## Developing

This project demonstrates the use of Event Sourced Entities, Subscriptions and Actions components.
To understand more about these components, see [Developing services](https://docs.kalix.io/services/)
and in particular the [Java section](https://docs.kalix.io/java/)

## Building

Use Maven to build your project:

```shell
mvn compile
```

## Running Locally

When running a Kalix application locally, at least two applications are required. The current Kalix application and its companion Kalix
Proxy.

To start the applications locally, call the following command:

```shell
mvn kalix:runAll
```

This command will start your Kalix application and a Kalix Proxy using the included [docker-compose.yml](.
/docker-compose.yml) file.

## Exercising the service

- Create wallet

```shell
curl -XPOST localhost:9000/wallet/w1/o1/100 
```

- Create wallet public api

```shell
curl -XPOST localhost:9000/public/wallet/w2/o2/100 
```

- Deposit funds

```shell
curl -XPATCH localhost:9000/wallet/w1/deposit/50 
```

- Withdraw funds

```shell
curl -XPATCH localhost:9000/wallet/w1/withdraw/20 
```

- Get wallet

```shell
curl localhost:9000/wallet/w1 
```