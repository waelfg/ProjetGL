#! /bin/sh
cd "$(dirname "$0")"/../../.. || exit 1

PATH=./src/test/script/launchers:"$PATH"
return_status=0

VERT='\033[0;32m'
ROUGE='\033[0;31m'
BLANC='\033[0;37m'
ROSE='\033[0;35m'

echo -e "${ROSE}TESTS INVALIDES: ${BLANC}"
for test in src/test/deca/codegen/invalid/provided/*.deca
do
    resultat=$(test_context "$test" 2>&1)
    comment=$(cat ${test} | cut -c '3-' |sed -n 5p $(test))
    if echo "$resultat" | grep -q "$comment"
    then
            echo -e "$test" ": ${VERT} Erreur attendue ${BLANC}"
    else
            echo -e "$test" ": ${ROUGE} Comportement inattendu ${BLANC}"
            return_status=1
    fi
done

echo -e "${ROSE}TESTS VALIDES: ${BLANC}"
for test in src/test/deca/codegen/valid/provided/*.deca
do
    fichier=$(echo ${test})
    resultat=$(test_context "$test" 2>&1)
    if echo "$resultat" | grep -q "$fichier"':[0-9]'
    then
            echo -e "$test" ": ${ROUGE} Erreur contextuelle inattendue${BLANC}"
            return_status=1
    elif echo "$resultat" | grep -q "Exception"
    then        echo -e "$test" ": ${ROUGE} Exception rattrapée inattendue${BLANC}"
            return_status=1
    else
            echo -e "$test" ": ${VERT} Succès attendu${BLANC}"
    fi
done
exit ${return_status}