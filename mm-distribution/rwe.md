EC2
---

# ssh to ec2 instance
 $ ssh -i ~/projects/wehner/aws_rwe1.pem ec2-user@54.247.78.61

# sync project to ec2-instance
 $ mvn install package -DskipTests
 $ mm-distribution/src/main/scripts/sync-to-ec2.sh 54.247.78.61
 
# copy distribution to ec2 instance (obsolete!)
 $ scp -r -i ~/projects/wehner/aws_rwe1.pem /Users/ralfwehner/projects/wehner/mediamagpie/mm-distribution/target/mm-distribution-0.1-SNAPSHOT-distribution/* ec2-user@ec2-54-247-33-138.eu-west-1.compute.amazonaws.com:

# Amazons Management Console:
 https://console.aws.amazon.com/ec2/home?region=eu-west-1#s=Instances

# hudson on ec2
 $ nohup java -jar jenkins.war > $LOGFILE 2>&1
 http://54.247.78.61:8080/pluginManager/?
 

Ich habe mir überlegt, dass das Tool Java Visual VM (jvisualvm) praktisch wäre, um sich einen Überblick über den Speicherverbrauch des tomcat und der Silverbuttet worker verschaffen zu können.
Damit das VisualVM sich gegen die JVMs auf den templates verbinden kann, muss auf dem entspr. template a) das Tool jstatd gestartet werden und b) der user e2-r benötigt eine Namesauflösung in der chroot Umgebung. (Hierzu bin ich auf Dirk zugegangen.)

Zusammengefasst gilt es die folgenden Schritte auf dem template auszuführen:
a) Starten des jstatd als user e2-r innerhalb der chroot-Umgebung

        Erzeugen einer Policydatei jstatd.all.policy mit dem Inhalt:

        grant codebase "file:${java.home}/../lib/tools.jar" {

           permission java.security.AllPermission;

        };

b) Sicherstellen, dass die Namensauflösung des Templates funktioniert. Hier bin ich auf Dirk (Dengelstube) zugegangen und wir haben es auf meinem Template e2-rwehner eingerichtet


TODOs
-----
# ggf. mysql nachinstallieren:
  $ yum install mysql
  $ yum install mysql-server
  
# setup mail server in ec2 installation

# introduce the Criteria Builder, eg:
        final CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery< Long > criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root< UsedPortalAddress > from = criteriaQuery.from(UsedPortalAddress.class);

        criteriaQuery.select(criteriaBuilder.count(criteriaQuery.from(UsedPortalAddress.class)));
        Predicate predicate1 = criteriaBuilder.equal(from.get("firstName"), firstname.trim());
        Predicate predicate2 = criteriaBuilder.equal(from.get("lastName"), lastname.trim());
        Predicate predicate3 = criteriaBuilder.equal(from.get("counter"), counterForQuery);
        criteriaQuery.where(criteriaBuilder.and(predicate1, predicate2, predicate3));
        Long foundSameUsedPortalAddresses = em.createQuery(criteriaQuery).getSingleResult();