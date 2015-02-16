# isos
Infrastructure System-of-Systems Modeling Framework

This prototype code demonstrates interoperable infrastructure systems simulation using the ISoS modeling framework [1] and the High Level Architecture [2] as discussed in [3].

A benchmark application is defined in the `edu.mit.isos.app.FederateController` class (in the `src/examples/java` source folder). This class contains a main method to execute a series of simulations with variable iterations per time step (`itr`), replications (`rep`), time step duration (`stp`), and simulation duration (`dur`). Single-threaded simulations run all simulation models in one thread (`singleThread` method). Distributed simulations run simulation models as HLA federates on threads (`multiThread` method). Command line arguments specify which federates to instantiate on a host as roles. Available roles include: `SOCIAL`, `WATER`, `ELECT`, and `PETROL`.

Please contact the author with any questions.

1. Grogan, P. T., and O. L. de Weck, "The ISoS Modeling Framework for infrastructure systems simulation", *IEEE Systems Journal*, 2015, in revision.
2. IEEE, "IEEE Standard for modeling and simulation (M&S) high level architecture (HLA) -- framework and rules," 2010, IEEE Std. 1516-2010.
3. Grogan, P. T., and O. L. de Weck, "Interoperable Infrastructure Systems Simulation using the High Level Architecture (HLA)", *IEEE Systems Journal*, 2015, under review.
