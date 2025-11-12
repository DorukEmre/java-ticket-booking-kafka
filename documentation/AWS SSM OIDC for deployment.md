# AWS SSM Agent — verify, control and CI deploy

## Remote, verify amazon-ssm-agent is running

``` bash
sudo systemctl status snap.amazon-ssm-agent.amazon-ssm-agent.service
```
or
```
sudo snap services amazon-ssm-agent
```

## To control it:

```
sudo snap start amazon-ssm-agent
sudo snap stop amazon-ssm-agent
sudo snap restart amazon-ssm-agent
```


## Local, AWS CLI:
Install:
```
https://docs.aws.amazon.com/cli/latest/userguide/getting-started-install.html
```

``` bash
aws --version

aws ec2 describe-instances

aws iam list-users

aws configure get region
```


## AWS Console, create IAM role for GitHub Actions using OIDC

- new IAM role: `GitHubDeployOIDC` Web Identity
  - Identity provider: `token.actions.githubusercontent.com`
  - Audience: `sts.amazonaws.com`

It will create a trust entiy for the provider:
``` json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": {
        "Federated": "arn:aws:iam::<ACCOUNT_ID>:oidc-provider/token.actions.githubusercontent.com"
      },
      "Action": "sts:AssumeRoleWithWebIdentity",
      "Condition": {
        "StringEquals": {
          "token.actions.githubusercontent.com:aud": "sts.amazonaws.com",
          "token.actions.githubusercontent.com:sub": "repo:<OWNER>/<REPO>:ref:refs/heads/main"
        }
      }
    }
  ]
}
```


## Add IAM policy for GitHub Actions (minimal SSM permissions)

Name: `GitHubSSMDeployPolicy`

Replace:
- <REGION>: AWS region where the instance is located
- <ACCOUNT_ID>: AWS account number
- <INSTANCE_ID>: Identifier for the EC2 instance

``` json
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Sid": "AllowSendCommandToSpecificInstances",
            "Effect": "Allow",
            "Action": [
                "ssm:SendCommand",
                "ssm:GetCommandInvocation"
            ],
            "Resource": [
                "*"
            ]
        },
        {
            "Sid": "AllowDescribeInstancesAndSSM",
            "Effect": "Allow",
            "Action": [
                "ssm:DescribeInstanceInformation",
                "ec2:DescribeInstances"
            ],
            "Resource": "*"
        }
    ]
}
```
