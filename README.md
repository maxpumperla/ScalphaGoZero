# ScalphaGoZero

ScalphaGoZero is an independent implementation of DeepMind's AlphaGo Zero in Scala, 
using [Deeplearning4J (DL4J)](https://deeplearning4j.org/) to run neural networks. 
You can either run experiments with models built in [DL4J directly](https://github.com/maxpumperla/ScalphaGoZero/blob/master/src/main/scala/org/deeplearning4j/scalphagozero/models/DualResnetModel.scala) 
or import prebuilt [Keras models](https://github.com/maxpumperla/ScalphaGoZero/blob/master/src/main/scala/org/deeplearning4j/scalphagozero/models/KerasModel.scala).

This project is a Scala port of the AlphaGo Zero module found in 
[Deep Learning and the Game of Go](https://github.com/maxpumperla/deep_learning_and_the_game_of_go).

## Getting started

Here's how to download the library, install it and run its main application:

```bash
git clone https://github.com/maxpumperla/ScalphaGoZero
cd ScalphaGoZero
mvn clean install exec:java
```

[This application]() will set up two opponents, simulate 10 games between the two with the
AlphaGo Zero methodology and 