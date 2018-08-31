


d <- read.csv("\\gitlab\\scheduling\\data\\dynamicOutput\\manuel\\out_epi10000_75.xml_experiment.csv", header=TRUE, sep=";")

deadline <- subset(d,d$algorithm=="deadline")

LCP_a <- subset(d, d$algorithm == "LCP_a")
PCP_a <- subset(d, d$algorithm == "PCP_a")
SPSS_a <- subset(d, d$algorithm == "SPSS_a")

LCP_a$algorithm = "LCP"
PCP_a$algorithm = "PCP"
SPSS_a$algorithm = "SPSS"

LCP_e <- subset(d, d$algorithm == "LCP_e")
PCP_e <- subset(d, d$algorithm == "PCP_e")
SPSS_e <- subset(d, d$algorithm == "SPSS_e")

LCP_e$algorithm = "LCP"
PCP_e$algorithm = "PCP"
SPSS_e$algorithm = "SPSS"

algo <- rbind(LCP_a, PCP_a, SPSS_a)
experiment <- rbind(LCP_e, PCP_e, SPSS_e)



jpeg(file = "\\gitlab\\scheduling\\data\\dynamicOutput\\manuel\\figures\\epi ms.jpeg",width=1000, height=500)

# ab hier malen. ylim gibt start und ende der y skala an
par(cex.lab=1.5)
par(cex.axis=1.5)
#boxplot(experiment$makespan ~ experiment$algorithm,ylim=c(5.5,8.5),ylab="makespan (h)", main="epi ms")
boxplot(experiment$makespan ~ experiment$algorithm,ylim=c(8.0,16.0),ylab="makespan (h)", main="epi ms")
abline(h=mean(deadline$makespan),lty=2)
lines(c(0.6,1.4), c(LCP_a$makespan, LCP_a$makespan), lwd=2, lty=8)
lines(c(1.6,2.4), c(PCP_a$makespan, PCP_a$makespan), lwd=2, lty=8)
lines(c(2.6,3.4), c(SPSS_a$makespan, SPSS_a$makespan), lwd=2, lty=8)
dev.off()

jpeg(file = "\\gitlab\\scheduling\\data\\dynamicOutput\\manuel\\figures\\epi cost.jpeg",width=1000, height=500)

# ab hier malen. ylim gibt start und ende der y skala an
par(cex.lab=1.5)
par(cex.axis=1.5)
#boxplot(experiment$makespan ~ experiment$algorithm,ylim=c(5.5,8.5),ylab="makespan (h)", main="epi ms")
boxplot(experiment$cost ~ experiment$algorithm, ylim=c(4000,5800), ylab="cost ($)", main="epi cost")

lines(c(0.6,1.4), c(LCP_a$cost, LCP_a$cost), lwd=2, lty=8)
lines(c(1.6,2.4), c(PCP_a$cost, PCP_a$cost), lwd=2, lty=8)
lines(c(2.6,3.4), c(SPSS_a$cost, SPSS_a$cost), lwd=2, lty=8)
dev.off()

##################################################################################################################################################

d <- read.csv("\\gitlab\\scheduling\\data\\dynamicOutput\\manuel\\out_inspi10000_75.xml_experiment.csv", header=TRUE, sep=";")

deadline <- subset(d,d$algorithm=="deadline")

LCP_a <- subset(d, d$algorithm == "LCP_a")
PCP_a <- subset(d, d$algorithm == "PCP_a")
SPSS_a <- subset(d, d$algorithm == "SPSS_a")

LCP_a$algorithm = "LCP"
PCP_a$algorithm = "PCP"
SPSS_a$algorithm = "SPSS"

LCP_e <- subset(d, d$algorithm == "LCP_e")
PCP_e <- subset(d, d$algorithm == "PCP_e")
SPSS_e <- subset(d, d$algorithm == "SPSS_e")

LCP_e$algorithm = "LCP"
PCP_e$algorithm = "PCP"
SPSS_e$algorithm = "SPSS"

algo <- rbind(LCP_a, PCP_a, SPSS_a)
experiment <- rbind(LCP_e, PCP_e, SPSS_e)



jpeg(file = "\\gitlab\\scheduling\\data\\dynamicOutput\\manuel\\figures\\inspi ms.jpeg",width=1000, height=500)

# ab hier malen. ylim gibt start und ende der y skala an
par(cex.lab=1.5)
par(cex.axis=1.5)
boxplot(experiment$makespan ~ experiment$algorithm, ylim=c(4.0,5.4), ylab="makespan (h)", main="inspi ms")
abline(h=mean(deadline$makespan),lty=2)
lines(c(0.6,1.4), c(LCP_a$makespan, LCP_a$makespan), lwd=2, lty=8)
lines(c(1.6,2.4), c(PCP_a$makespan, PCP_a$makespan), lwd=2, lty=8)
lines(c(2.6,3.4), c(SPSS_a$makespan, SPSS_a$makespan), lwd=2, lty=8)
dev.off()

jpeg(file = "\\gitlab\\scheduling\\data\\dynamicOutput\\manuel\\figures\\inspi cost.jpeg",width=1000, height=500)

# ab hier malen. ylim gibt start und ende der y skala an
par(cex.lab=1.5)
par(cex.axis=1.5)
boxplot(experiment$cost ~ experiment$algorithm, ylim=c(2800,4000), ylab="cost ($)", main="inspi cost")

lines(c(0.6,1.4), c(LCP_a$cost, LCP_a$cost), lwd=2, lty=8)
lines(c(1.6,2.4), c(PCP_a$cost, PCP_a$cost), lwd=2, lty=8)
lines(c(2.6,3.4), c(SPSS_a$cost, SPSS_a$cost), lwd=2, lty=8)
dev.off()

##################################################################################################################################################

d <- read.csv("\\gitlab\\scheduling\\data\\dynamicOutput\\manuel\\out_sipht10000_75.xml_experiment.csv", header=TRUE, sep=";")

deadline <- subset(d,d$algorithm=="deadline")

LCP_a <- subset(d, d$algorithm == "LCP_a")
PCP_a <- subset(d, d$algorithm == "PCP_a")
SPSS_a <- subset(d, d$algorithm == "SPSS_a")

LCP_a$algorithm = "LCP"
PCP_a$algorithm = "PCP"
SPSS_a$algorithm = "SPSS"

LCP_e <- subset(d, d$algorithm == "LCP_e")
PCP_e <- subset(d, d$algorithm == "PCP_e")
SPSS_e <- subset(d, d$algorithm == "SPSS_e")

LCP_e$algorithm = "LCP"
PCP_e$algorithm = "PCP"
SPSS_e$algorithm = "SPSS"

algo <- rbind(LCP_a, PCP_a, SPSS_a)
experiment <- rbind(LCP_e, PCP_e, SPSS_e)



jpeg(file = "\\gitlab\\scheduling\\data\\dynamicOutput\\manuel\\figures\\sipht ms.jpeg",width=1000, height=500)

# ab hier malen. ylim gibt start und ende der y skala an
par(cex.lab=1.5)
par(cex.axis=1.5)
boxplot(experiment$makespan ~ experiment$algorithm, ylim=c(5.5,8.0), ylab="makespan (h)", main="sipht ms")
abline(h=mean(deadline$makespan),lty=2)
lines(c(0.6,1.4), c(LCP_a$makespan, LCP_a$makespan), lwd=2, lty=8)
lines(c(1.6,2.4), c(PCP_a$makespan, PCP_a$makespan), lwd=2, lty=8)
lines(c(2.6,3.4), c(SPSS_a$makespan, SPSS_a$makespan), lwd=2, lty=8)
dev.off()

jpeg(file = "\\gitlab\\scheduling\\data\\dynamicOutput\\manuel\\figures\\sipht cost.jpeg",width=1000, height=500)

# ab hier malen. ylim gibt start und ende der y skala an
par(cex.lab=1.5)
par(cex.axis=1.5)
boxplot(experiment$cost ~ experiment$algorithm, ylim=c(650,950), ylab="cost ($)", main="sipht cost")

lines(c(0.6,1.4), c(LCP_a$cost, LCP_a$cost), lwd=2, lty=8)
lines(c(1.6,2.4), c(PCP_a$cost, PCP_a$cost), lwd=2, lty=8)
lines(c(2.6,3.4), c(SPSS_a$cost, SPSS_a$cost), lwd=2, lty=8)
dev.off()

##################################################################################################################################################

d <- read.csv("\\gitlab\\scheduling\\data\\dynamicOutput\\manuel\\out_cyber10000_75.xml_experiment.csv", header=TRUE, sep=";")

deadline <- subset(d,d$algorithm=="deadline")

LCP_a <- subset(d, d$algorithm == "LCP_a")
PCP_a <- subset(d, d$algorithm == "PCP_a")
SPSS_a <- subset(d, d$algorithm == "SPSS_a")

LCP_a$algorithm = "LCP"
PCP_a$algorithm = "PCP"
SPSS_a$algorithm = "SPSS"

LCP_e <- subset(d, d$algorithm == "LCP_e")
PCP_e <- subset(d, d$algorithm == "PCP_e")
SPSS_e <- subset(d, d$algorithm == "SPSS_e")

LCP_e$algorithm = "LCP"
PCP_e$algorithm = "PCP"
SPSS_e$algorithm = "SPSS"

algo <- rbind(LCP_a, PCP_a, SPSS_a)
experiment <- rbind(LCP_e, PCP_e, SPSS_e)



jpeg(file = "\\gitlab\\scheduling\\data\\dynamicOutput\\manuel\\figures\\cyber ms.jpeg",width=1000, height=500)

# ab hier malen. ylim gibt start und ende der y skala an
par(cex.lab=1.5)
par(cex.axis=1.5)
boxplot(experiment$makespan ~ experiment$algorithm, ylim=c(4.2,5.6), ylab="makespan (h)", main="cyber ms")
abline(h=mean(deadline$makespan),lty=2)
lines(c(0.6,1.4), c(LCP_a$makespan, LCP_a$makespan), lwd=2, lty=8)
lines(c(1.6,2.4), c(PCP_a$makespan, PCP_a$makespan), lwd=2, lty=8)
lines(c(2.6,3.4), c(SPSS_a$makespan, SPSS_a$makespan), lwd=2, lty=8)
dev.off()

jpeg(file = "\\gitlab\\scheduling\\data\\dynamicOutput\\manuel\\figures\\cyber cost.jpeg",width=1000, height=500)

# ab hier malen. ylim gibt start und ende der y skala an
par(cex.lab=1.5)
par(cex.axis=1.5)
boxplot(experiment$cost ~ experiment$algorithm, ylim=c(240,380), ylab="cost ($)", main="cyber cost")

lines(c(0.6,1.4), c(LCP_a$cost, LCP_a$cost), lwd=2, lty=8)
lines(c(1.6,2.4), c(PCP_a$cost, PCP_a$cost), lwd=2, lty=8)
lines(c(2.6,3.4), c(SPSS_a$cost, SPSS_a$cost), lwd=2, lty=8)
dev.off()

##################################################################################################################################################

d <- read.csv("\\gitlab\\scheduling\\data\\dynamicOutput\\manuel\\out_montage10000_75.xml_experiment.csv", header=TRUE, sep=";")

deadline <- subset(d,d$algorithm=="deadline")

LCP_a <- subset(d, d$algorithm == "LCP_a")
PCP_a <- subset(d, d$algorithm == "PCP_a")
SPSS_a <- subset(d, d$algorithm == "SPSS_a")

LCP_a$algorithm = "LCP"
PCP_a$algorithm = "PCP"
SPSS_a$algorithm = "SPSS"

LCP_e <- subset(d, d$algorithm == "LCP_e")
PCP_e <- subset(d, d$algorithm == "PCP_e")
SPSS_e <- subset(d, d$algorithm == "SPSS_e")

LCP_e$algorithm = "LCP"
PCP_e$algorithm = "PCP"
SPSS_e$algorithm = "SPSS"

algo <- rbind(LCP_a, PCP_a, SPSS_a)
experiment <- rbind(LCP_e, PCP_e, SPSS_e)



jpeg(file = "\\gitlab\\scheduling\\data\\dynamicOutput\\manuel\\figures\\montage ms.jpeg",width=1000, height=500)

# ab hier malen. ylim gibt start und ende der y skala an
par(cex.lab=1.5)
par(cex.axis=1.5)
boxplot(experiment$makespan ~ experiment$algorithm, ylim=c(5.6,6.4), ylab="makespan (h)", main="montage ms")
abline(h=mean(deadline$makespan),lty=2)
lines(c(0.6,1.4), c(LCP_a$makespan, LCP_a$makespan), lwd=2, lty=8)
lines(c(1.6,2.4), c(PCP_a$makespan, PCP_a$makespan), lwd=2, lty=8)
lines(c(2.6,3.4), c(SPSS_a$makespan, SPSS_a$makespan), lwd=2, lty=8)
dev.off()

jpeg(file = "\\gitlab\\scheduling\\data\\dynamicOutput\\manuel\\figures\\montage cost.jpeg",width=1000, height=500)

# ab hier malen. ylim gibt start und ende der y skala an
par(cex.lab=1.5)
par(cex.axis=1.5)
boxplot(experiment$cost ~ experiment$algorithm, ylim=c(2900,3200), ylab="cost ($)", main="montage cost")

lines(c(0.6,1.4), c(LCP_a$cost, LCP_a$cost), lwd=2, lty=8)
lines(c(1.6,2.4), c(PCP_a$cost, PCP_a$cost), lwd=2, lty=8)
lines(c(2.6,3.4), c(SPSS_a$cost, SPSS_a$cost), lwd=2, lty=8)
dev.off()

##################################################################################################################################################

