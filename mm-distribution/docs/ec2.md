#EC2

## Preparing node with puppet installation

##### 1 Copy remote script to node
    $ scp -i  ~/projects/wehner/aws_rwe1.pem ./mm-distribution/src/main/scripts/setupNode/update_note.sh ec2-user@54.171.82.164:~/

##### 2 Execute on node
    $ ssh puppet-test -t "sudo -u root sh /home/ec2-user/update_note.sh"
    
### Install puppet 3 on ec2 instance



```bash
  ...
  max_connections=10
  key_buffer_size=32M
  query_cache_size=32M
  ...
´´´