echo "What is the commit reason?"
read commit
git init
git commit -a -m "$commit"
git push
