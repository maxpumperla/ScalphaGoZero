from alphago_zero import *

dual_res = dual_residual_network((10, 19, 19))
dual_res.compile(loss=['mse', 'categorical_crossentropy'],optimizer='sgd',metrics=['accuracy'])
dual_res.save("dual_res.h5")

sep_res_policy, sep_res_value = separate_residual_network((10, 19, 19))
sep_res_policy.compile(loss='mse',optimizer='sgd',metrics=['accuracy'])
sep_res_policy.save("sep_res_policy.h5")
sep_res_value.compile(loss='mse',optimizer='sgd',metrics=['accuracy'])
sep_res_value.save("sep_res_value.h5")

dual_conv = dual_conv_network((10, 19, 19))
dual_conv.compile(loss=['mse', 'categorical_crossentropy'],optimizer='sgd',metrics=['accuracy'])
dual_conv.save("dual_conv.h5")

sep_conv_policy, sep_conv_value = separate_conv_network((10, 19, 19))
sep_conv_policy.compile(loss='mse',optimizer='sgd',metrics=['accuracy'])
sep_conv_policy.save("sep_conv_policy.h5")
sep_conv_value.compile(loss='mse',optimizer='sgd',metrics=['accuracy'])
sep_conv_value.save("sep_conv_value.h5")