# QRCodeRW

-Camera ile veya barcode okuyucu ile calisabilir durumda

text watcher kullanımı ile ilgili kısım (HomeActivity.kt de yaziyordu onun yerine live data kullandigin icin kaldirdin):

/*

    private var textWatcher: TextWatcher? = null
    private fun getTextWatcher(): TextWatcher {
        return object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.d("textTAG", s.toString())
                if (s?.isNotEmpty()!! && s.isNotBlank())
                    token += s[start] + ""
                when (token.length) {
                    in 1..10 -> {
                        cTimer.cancel()
                        cTimer.start()
                    }
                    11 -> {
                        binding.progressBarHome.visibility = View.VISIBLE
                        val profile = sendQuery()
                        cTimer.cancel()
                        profile?.idNumber = token
                        token = ""
                        beginTransactionProfile(profile)
                    }
                    else -> {
                        Log.e("Error", "something is going wrong" + token.length)
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }
    }

*/

