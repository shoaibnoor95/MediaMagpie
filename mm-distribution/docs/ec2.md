#EC2

## Preparing node with puppet installation

### Install puppet 3 on the node

##### 1 Copy remote script to node
    $ scp -i  ~/projects/wehner/aws_rwe1.pem ./puppet/src/main/scripts/update_note.sh ec2-user@54.171.82.164:~/

##### 2 Execute on node
    $ ssh puppet-test -t "sudo -u root sh /home/ec2-user/update_note.sh"
    
See also: https://docs.puppetlabs.com/puppet/3/reference/modules_fundamentals.html

## TODO:

0 specify puppet, use hiera (https://docs.puppetlabs.com/puppet/3/reference/lang_classes.html#using-hierainclude)
1. build the artifacts
2. pack all artifacts into a zip
3. copy zip to node

On node side:
4. extract / verify the aritifacts
5. run puppet


```bash
  ...
  max_connections=10
  key_buffer_size=32M
  query_cache_size=32M
  ...
´´´