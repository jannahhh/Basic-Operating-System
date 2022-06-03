
# Basic Operating System.

This is our project from "CSEN 602: Operating Systems" course. The project is a simulation to an operating system implemented completely using Java.

### The project was divided into two parts:
Part one is about the operating system itself. It contained a user-mode where all the logic took place, and a kernel-mode where all system calls and memory access was handled.
We implemented a scheduler based on Round Robin Algorithm to handle processes. We also implemented a mutex to manage resources among the processes.

As for part two, we implemented a simulation of a memory and a hard-disk. As required, the memory had to fit only two processes in it.
While the third process had to be placed in the hard-disk. Swapping between the memory and hard-disk took place to compensate lack of space in our memory.

