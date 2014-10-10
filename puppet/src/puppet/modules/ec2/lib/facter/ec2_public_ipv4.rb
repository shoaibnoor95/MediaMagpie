# ec2_public_ipv4.rb
Facter.add("ec2_public_ipv4") do
    setcode do
        public_ip = Facter::Util::Resolution.exec("curl -s http://169.254.169.254/latest/meta-data/public-ipv4 --max-time 1") 
        if public_ip.to_s == ''
            public_ip = "localhost"
        end
        public_ip
    end
end