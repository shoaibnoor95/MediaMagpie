# -*- mode: ruby -*-
# vi: set ft=ruby

## update to java7 version
class portal::install {

    exec { "refresh cache":
        command => "/usr/bin/yum clean all"
    }

    # download oracle's jdk-7 rpm
    exec { 'download-java-rpm':
        path    => '/bin:/usr/bin',
        command => "curl -s  -o /tmp/jdk-7u40.rpm https://s3-eu-west-1.amazonaws.com/yum-repos/jdk-7u40-linux-x64.rpm",
        creates => "/tmp/jdk-7.rpm"
    }
    package { "jdk":
        ensure => "absent"
    }
#  exec { "remove-old-jdk":
#    path    => '/bin:/usr/bin',
#    command => "rpm -ev jdk",
#    require  => Exec['download-java-rpm']
#  }
    exec { "install-jdk":
        path    => '/bin:/usr/bin',
        command => "rpm -Uvh /tmp/jdk-7u40.rpm",
#    require  => [Exec['download-java-rpm'],Exec['remove-old-jdk']]
        require  => [Exec['download-java-rpm'],Package['jdk']]
    }

    package { "ImageMagick":
        ensure => latest
    }    
}

