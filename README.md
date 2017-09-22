# Testing Layers

Medium Article to Come!

The idea here is to illustrate that "isolation" if often termed wrong by programmers, and that
testing "in isolation" really only means isolating yourself from external stuff, like databases,
network calls, and the like.

Here we have a kotlin project which consists of 3 layers that we're concerned with testing in:

* Presentation
* Domain
* Entities

Entities are tested in isolation in that they only concern themselves and other entities, and
perform simple validation of their inputs.

Domain objects are tested in isolation but use fully qualified entities as is to be expected.  The
repositories are interfaced off due to dependency inversion and it is simple to see that we can
fully test the abstract logic here.

Presentation are View models, in the MVVM sense of the word, and utilize fully qualified domain
objects and entities.  Herein the difference lies.  We do not try to isolate ourselves from our
domain, because our domain IS our application, and we should make sure our presentation layer
integrates well with our domain layer, without needing to concern ourselves with lower levels (which
do not exist in this application but are implied.)
