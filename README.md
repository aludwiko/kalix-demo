# Kalix Demo Application

## Designing

To understand the Kalix concepts that are the basis for this example,
see [Designing services](https://docs.kalix.io/java/development-process.html) in the documentation. Keep in mind that this sample implements
two Saga Patter solutions for transferring funds between wallets: choreography and orchestration. Some code is redundant if we select one of
the solutions.

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

To start the applications locally with the console run:

```shell
kalix local run
```

and go to `http://localhost:3000/` to see the service and available components.

## Exercising the service

- Export app url

```shell
#export KALIX_DEMO_URL=https://solitary-paper-3969.eu-central-1.kalix.app
export KALIX_DEMO_URL=localhost:9000
```

- Create wallet

```shell
curl -i $KALIX_DEMO_URL/hello/andrzej 
```

- Create wallet

```shell
curl -XPOST $KALIX_DEMO_URL/wallet/wallet1/owner1/100 
```

- Create wallet public api

```shell
curl -XPOST $KALIX_DEMO_URL/public/wallet/wallet2/owner2/100 
```

- Deposit funds

```shell
curl -XPATCH $KALIX_DEMO_URL/wallet/wallet1/deposit/50 
```

- Withdraw funds

```shell
curl -XPATCH $KALIX_DEMO_URL/wallet/wallet1/withdraw/20 
```

- Get wallet

```shell
curl $KALIX_DEMO_URL/wallet/wallet1 
```

- Delete wallet

```shell
curl -XDELETE $KALIX_DEMO_URL/wallet/wallet1 
```

- Find with balance below 200

```shell
curl $KALIX_DEMO_URL/wallet/by-balance-below/200 
```

- Find with by owner

```shell
curl $KALIX_DEMO_URL/wallet/by-owner/owner1 
```

- Transfer funds - choreography

```shell
curl -XPATCH $KALIX_DEMO_URL/wallet/wallet1/transfer/wallet2/20 
```

- Transfer funds - orchestration (Workflow Entity)

```shell
curl -XPOST $KALIX_DEMO_URL/transfer/transfer1/wallet1/wallet2/20 
```

- Get transfer state

```shell
curl  $KALIX_DEMO_URL/transfer/transfer1
```

## Deploying the service

Follow the quickstart [tutorial](https://docs.kalix.io/java/quickstart/sc-eventsourced-entity-java.html#_package_and_deploy_your_service) or
operating [documentation](https://docs.kalix.io/operations/deploy-service.html#_deploy).

