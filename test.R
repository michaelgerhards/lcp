library(data.table) 

filename <- "D:/Miroslav/sources/paperMichael/experiment.csv"

experiment <- fread(filename, sep = ";", header=T, select=1:3 ) #Probably you want to select only columns one to three

### How to get column names? Function colnames
names <- colnames(experiment)
#Names contain values algorithm, makespan and cost


### How to set new column names?
newColumnNames <- c("Name 1", "Name 2", "Name 3") #Create a vector of new column names
colnames(experiment) <- newColumnNames #set

# Column names are now "Name 1, 2 and 3"
# but we want old names, i.e. algorithm, makespan and cost
colnames(experiment) <- names

### how to remove cheapest here, makespan = 0
costExperiment <- experiment[experiment$makespan != 0]
boxplot(makespan ~ algorithm, ylab="makespan", xlab="algorithm", data=costExperiment)

#####how to remove deadline here, cost = 0
makespanExperiment <- experiment[experiment$cost != 0]
#without scaling of the Y axis
boxplot(cost ~ algorithm, ylab="Cost (US$)", xlab="algorithm", data=makespanExperiment)
#with scaling of the Y axis
boxplot(cost/10^6 ~ algorithm, ylab="Cost (million US$)", xlab="algorithm", data=makespanExperiment)

#However, if you want to remove all entries that contain the word cheapest as a value in algorithm column, 
#use something like this:
noCheapestExperiment <- experiment[experiment$algorithm != "cheapest"]
# In your case, you would get the same result as costExperiment

#without scaling of the Y axis
boxplot(makespan ~ algorithm, ylab="Makespan (seconds)", xlab="algorithm", data=noCheapestExperiment)
#with scaling of the Y axis
boxplot(makespan/1000 ~ algorithm, ylab="Makespan (thousands of seconds)", xlab="algorithm", data=noCheapestExperiment)

