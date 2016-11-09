# Distributed_Algorithms

# HOW TO RUN
Run run.sh script from your directory - by hand exemplary or cd to this directory and ./run.sh

If you are on Windows - modify your run.sh file!!

# Exercise 1.3: Distribution of Information

i. Implement the Flooding algorithm with acknowledgments using the simulation
  framework teachnet (provided at the ISIS course website). After finishing the
  implementation, test your algorithm on a ring topology and compare the
  amount of messages sent with the “Broadcast on Unidirectional Rings” using
  the same topology.

ii. Implement the Echo algorithm using teachnet and evaluate the correctness of
  the amount of messages sent to be (2e) on various topologies. Highlight all
  edges that are part of the spanning tree.

iii. An improvement of the Echo algorithm has been introduced (see lecture) that
  sends a set of tabu node IDs together with an explorer. Examine the behavior
  of the algorithm compared to the classical Echo algorithm in terms of message
  reduction under the assumption of the following topologies:
   a) Bidiredtional ring with n nodes
    b) Binary X-tree of height h (with 2 h+1 -1 nodes)
  
  Additional notes and assessment:

 important parts of the implementation have to be annotated with comments
 each exercise has to be completed handled in teams of 3-4 students
 the exercise sheet is successfully completed, if exercise 1 was presented and
the solution was explained satisfactorily
