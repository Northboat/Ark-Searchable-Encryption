# Ark Searchable Encryption

> [基于 Pairing 的 SE 算法仿真](https://arkrypto.github.io/pages/752eb6/)

可搜索加密仿真，基于 JPBC 对一些基于 Pairing 的可搜索加密算法的复现，并测试其加密、陷门以及配对的时间开销，代码在 simulation 目录下

1. SPWSE Ⅰ √
2. SPWSE Ⅱ √
2. PAUKS √
4. SA-PAEKS √
5. dIBAEKS √
6. DuMSE ×
7. pMatch √
8. CR-IMA √
9. TuCR ⍻
10. Tu2CKS ⍻
11. PAEKS √
12. TMS √
13. TBEKS √
14. Gu2CKS √
15. FIPECK √
16. SCF √
17. PECKS √
18. AP √
19. PAKS √
19. DPREKS
19. PREKS

~~IPFE：基于属性加密的可搜索加密四叉树仿真~~

环境

- JDK 17
- JPBC 2.0.0
- IDEA 2022.3
- Maven 3.9.1
