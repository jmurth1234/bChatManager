echo "What is the commit reason?"
read commit
git init
git add *
git commit -m '$commit'
git push
