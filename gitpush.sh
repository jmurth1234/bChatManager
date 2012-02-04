echo "What is the commit reason? Plz say below...."
read commit
git init
git commit -a -m "$commit"
git push
